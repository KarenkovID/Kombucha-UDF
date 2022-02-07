import org.gradle.api.Project
import tools.forma.deps.*
import tools.forma.target.FormaMimicTarget
import tools.forma.target.FormaTarget
import tools.forma.target.TargetTemplate
import java.io.File

val DepType.names: List<NameSpec>
    get(): List<NameSpec> = filterIsInstance(NameSpec::class.java)

val DepType.targets: List<TargetSpec>
    get(): List<TargetSpec> = filterIsInstance(TargetSpec::class.java)

val DepType.files: List<FileSpec>
    get(): List<FileSpec> = filterIsInstance(FileSpec::class.java)

infix operator fun FormaDependency.plus(dep: FormaDependency): MixedDependency = MixedDependency(
    dependency.names + dep.dependency.names,
    dependency.targets + dep.dependency.targets,
    dependency.files + dep.dependency.files
)

inline fun <reified T : FormaDependency> emptyDependency(): T = when {
    T::class == FormaDependency::class -> EmptyDependency as T
    T::class == NamedDependency::class -> NamedDependency() as T
    T::class == FileDependency::class -> FileDependency() as T
    T::class == TargetDependency::class -> TargetDependency() as T
    T::class == MixedDependency::class -> MixedDependency() as T
    else -> throw IllegalArgumentException("Illegal Empty dependency, expected ${T::class.simpleName}")
}

fun FormaDependency.forEach(
    nameAction: (NameSpec) -> Unit = {},
    targetAction: (TargetSpec) -> Unit = {},
    fileAction: (FileSpec) -> Unit = {},
    platformAction: (PlatformSpec) -> Unit = {}
) {
    dependency.forEach loop@{ spec ->
        return@loop when (spec) {
            is TargetSpec -> targetAction(spec)
            is NameSpec -> nameAction(spec)
            is PlatformSpec -> platformAction(spec)
            is FileSpec -> fileAction(spec)
        }
    }
}

internal fun FormaDependency.hasConfigType(configType: ConfigurationType): Boolean {
    dependency.forEach { dep ->
        if (dep.config == configType) return true
    }
    return false
}

fun deps(vararg names: String): NamedDependency = transitiveDeps(names = *names, transitive = false)

fun platform(vararg names: String): PlatformDependency = transitivePlatform(*names, transitive = false)

fun transitivePlatform(vararg names: String, transitive: Boolean = true): PlatformDependency =
    PlatformDependency(names.toList().map { PlatformSpec(it, Implementation, transitive) })

fun transitiveDeps(vararg names: String, transitive: Boolean = true): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Implementation, transitive) })

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "Deprecated in favor of targets version of this function:\n" +
            "deps(target(\":name\"))"
)
fun deps(vararg projects: Project): TargetDependency =
    TargetDependency(projects.toList().map { TargetSpec(it.target, Implementation) })

fun deps(vararg targets: FormaTarget): TargetDependency =
    TargetDependency(targets.toList().map { TargetSpec(it, Implementation) })

fun deps(vararg files: File): FileDependency =
    FileDependency(files.toList().map { FileSpec(it, Implementation) })

fun deps(vararg dependencies: NamedDependency): NamedDependency =
    dependencies.flatMap { it.names }.let(::NamedDependency)

fun deps(vararg dependencies: TargetDependency): TargetDependency =
    dependencies.flatMap { it.targets }.let(::TargetDependency)

fun kapt(vararg names: String): NamedDependency =
    NamedDependency(names.toList().map { NameSpec(it, Kapt, true) })

val String.dep: NamedDependency get() = deps(this)
val String.transitiveDep: NamedDependency get() = transitiveDeps(this)

val String.kapt: NamedDependency get() = kapt(this)

// TODO: maybe should use exclusive naming?
val Project.target: FormaTarget get() = FormaTarget(path)

fun target(path: String) = deps(FormaTarget(path))
fun mimicTarget(path: String, mimicTarget: TargetTemplate) = deps(FormaMimicTarget(path, mimicTarget))