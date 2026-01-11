package foundation.openstore.kitten.test.graph

import foundation.openstore.kitten.api.Injector
import io.github.classgraph.ClassGraph
import kotlin.use

fun checkGraph(name: String, onChecked: (Boolean, List<Injector<*>>) -> Unit) {
    val inejctors =  ClassGraph()
        .enableClassInfo()
        .scan()
        .use { scanResult ->
            scanResult
                .getSubclasses(Injector::class.java)
                .loadClasses()
                .map { it.kotlin }
                .filter { it.objectInstance != null }
                .map { it.objectInstance as Injector<*> }
        }

    println("Found injectors in graph [$name]:")
    inejctors
        .sortedBy { it.isInit() }
        .forEach {
            println("  - ${it::class.qualifiedName}: [${if (it.isInit()) "INIT" else "SKIP"}]")
        }

    val skipped = inejctors.filter { !it.isInit() }

    onChecked(skipped.isEmpty(), skipped)
}
