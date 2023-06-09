package pt.isel.turngamesfw.http.model

import pt.isel.turngamesfw.http.Rels
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.function.ServerRequest.Headers
import pt.isel.turngamesfw.http.Uris
import java.net.URI

data class HttpError(val status: Int, val message: String)

data class SirenModel<T> (
    @JsonProperty("class")
    val clazz: List<String>,
    val properties: T,
    val links: List<LinkModel>,
    val entities: List<EntityModel<*>>,
    val actions: List<ActionModel>
)

class LinkRelation(val value: String)

data class LinkModel(
    val rel: List<String>,
    val href: String
)

data class EntityModel<T>(
    val properties: T,
    val links: List<LinkModel>,
    val rel: List<String>
)

data class ActionModel(
    val name: String,
    val href: String,
    val method: String,
    val type: String?,
    val fields: List<FieldModel>
)

data class FieldModel(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val name: String,
    val type: String,
    val value: String? = null
)

class SirenBuilderScope<T>(
    val properties: T
) {
    private val links = mutableListOf<LinkModel>()
    private val entities = mutableListOf<EntityModel<*>>()
    private val classes = mutableListOf<String>()
    private val actions = mutableListOf<ActionModel>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun linkSelf(href: URI) {
        link(href, Rels.SELF)
    }

    fun linkSelf(href: String) {
        linkSelf(URI(href))
    }

    fun link(href: URI, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), href.toASCIIString()))
    }
    fun link(href: String, rel: LinkRelation) {
        link(URI(href), rel)
    }

    fun <U> entity(value: U, rel: LinkRelation, block: EntityBuilderScope<U>.() -> Unit) {
        val scope = EntityBuilderScope(value, listOf(rel.value))
        scope.block()
        entities.add(scope.build())
    }

    fun action(
        name: String,
        href: URI,
        method: HttpMethod,
        type: String? = null,
        block: ActionBuilderScope.() -> Unit
    ) {
        val scope = ActionBuilderScope(name, href, method, type)
        scope.block()
        actions.add(scope.build())
    }

    fun build(): SirenModel<T> = SirenModel(
        clazz = classes,
        properties = properties,
        links = links,
        entities = entities,
        actions = actions
    )
}

class EntityBuilderScope<T>(
    val properties: T,
    val rel: List<String>
) {
    private val links = mutableListOf<LinkModel>()

    fun link(href: URI, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), href.toASCIIString()))
    }
    fun linkSelf(href: URI) {
        link(href, Rels.SELF)
    }

    fun build(): EntityModel<T> = EntityModel(
        properties = properties,
        links = links,
        rel = rel
    )
}

class ActionBuilderScope(
    private val name: String,
    private val href: URI,
    private val method: HttpMethod,
    private val type: String? = null
) {
    private val fields = mutableListOf<FieldModel>()

    fun textField(name: String, classes: List<String> = listOf()) {
        fields.add(FieldModel(classes, name, "text"))
    }

    fun numberField(name: String, classes: List<String> = listOf()) {
        fields.add(FieldModel(classes, name, "number"))
    }

    fun hiddenField(name: String, value: String, classes: List<String> = listOf()) {
        fields.add(FieldModel(classes, name, "hidden", value))
    }

    fun passwordField(name: String, classes: List<String> = listOf()) {
        fields.add(FieldModel(classes, name, "password"))
    }

    fun build() = ActionModel(name, href.toASCIIString(), method.name(), type, fields)
}

fun <T> siren(value: T, block: SirenBuilderScope<T>.() -> Unit): SirenModel<T> {
    val scope = SirenBuilderScope(value)
    scope.block()
    return scope.build()
}

fun Any.toResponseEntity(
    status: HttpStatus = HttpStatus.OK,
    contentType: String = "application/vnd.siren+json",
    headers: HttpHeaders? = null,
    others: (ResponseEntity.BodyBuilder) -> Unit = {},
): ResponseEntity<*> {
    val res = ResponseEntity.status(status)
        .header("content-type", contentType)

    headers?.forEach { h ->
        res.header(h.key, h.value[0].toString())
    }

    others(res)

    return res.body(this)
}