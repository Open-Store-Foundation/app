package foundation.openstore.kitten.api

fun interface ComponentProvider<D, C : Component> {
    operator fun get(data: D): C
}
