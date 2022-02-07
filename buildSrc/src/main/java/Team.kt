import tools.forma.android.owner.Person
import tools.forma.android.owner.Team

object Users {

    val ikarenkov = Person(
        fullName = "Igor Karenkov",
        userName = "ikarenkov",
        email = "karenkovigor@gmail.com"
    )

}

object Teams {
    val core = Team(
        name = "Core",
        emailAlias = "android-core@forma.tools",
        leads = arrayOf(
            Users.ikarenkov
        )
    )
}