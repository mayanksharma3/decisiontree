import com.sun.source.tree.Tree
import java.io.File
import kotlin.math.log2

interface TreeNode

data class Node(val attribute: String, val children: List<TreeNode>): TreeNode

data class Leaf(val attribute: String): TreeNode

fun main() {
    val fileData = File("test.csv").readLines()
    val header = fileData.first().split(",").map { it.trim() }
    val data = fileData.drop(1).map { it.trim().split(",").map { c -> c.trim() } }
    println(decisionTree(header, data, "dataset"))
}

fun decisionTree(header: List<String>, data: List<List<String>>, nodeAttribute: String): TreeNode {
    val dataSetEntropy = entropy(data, nodeAttribute)
    val attributeAndGains = mutableMapOf<String, Double>()
    header.dropLast(1).forEach {
        attributeAndGains[it] = informationGain(dataSetEntropy, it, header, data)
    }
    val bestAttribute = attributeAndGains.maxByOrNull { it.value }!!
    if(bestAttribute.value == -0.0) {
        return Leaf(nodeAttribute)
    }
    val children = data.groupBy { it[header.indexOf(bestAttribute.key)] }.map { decisionTree(header, it.value, it.key) }
    return Node(bestAttribute.key, children)
}

private fun informationGain(datasetEntropy: Double, attribute: String, header: List<String>, data: List<List<String>>): Double {
    println("Calculating Information Gain for attribute $attribute")
    val groupedOnAttribute = data.groupBy { it[header.indexOf(attribute)] }
    println("Data proportions for attribute $attribute")
    groupedOnAttribute.forEach { println("${it.key}: ${it.value.size}/${data.size}") }
    val igSummation = groupedOnAttribute.map { (it.value.size / data.size.toDouble()) * entropy(it.value, it.key) }.sum()
    val informationGain = datasetEntropy - igSummation
    println("Information Gain: $informationGain")
    return informationGain
}

private fun entropy(data: List<List<String>>, name: String): Double {
    println("Calculating entropy for $name")
    val groupedData = data.groupBy { it.last() }.map { (it.value.size / data.size.toDouble()) }
    print("Data split: ")
    println(data.groupBy { it.last() }.map { it.key to it.value.size / data.size.toDouble() })
    print("Entropy: ")
    val entropy = groupedData.sumOf { it * log2(it) } * -1
    println(entropy)
    return entropy
}