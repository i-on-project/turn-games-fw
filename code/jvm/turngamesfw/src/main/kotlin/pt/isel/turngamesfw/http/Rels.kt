package pt.isel.turngamesfw.http

import pt.isel.turngamesfw.http.model.LinkRelation


object Rels {
    // Main repo tree URL
    private val REPO_DOCS = "https://github.com/i-on-project/turn-games-fw//tree/main/code/jvm/turngamesfw/docs"

    // Decode location of relation documentation
    private fun doc(lr: String) = "$REPO_DOCS/rels/$lr.txt"

    val SELF = LinkRelation("self")

    // Custom relations
    val HOME = LinkRelation("home")
    val USERS = LinkRelation(doc("users"))
    val ME = LinkRelation(doc("me"))
    val GAMES = LinkRelation(doc("games"))
    val MATCHES = LinkRelation(doc("matches"))
    val LOGIN = LinkRelation(doc("login"))
    val REGISTER = LinkRelation(doc("register"))

    // Definitions managed by iana (https://www.iana.org/assignments/link-relations/link-relations.xhtml)
    val AUTHOR = LinkRelation("author")
    val RELATED = LinkRelation("related")
    val ABOUT = LinkRelation("about")

    // List Navigation relations
    val FIRST = LinkRelation("first")
    val LAST = LinkRelation("last")
    val NEXT = LinkRelation("next")
    val PREVIOUS = LinkRelation("prev")

    val ITEM = LinkRelation("item")
}
