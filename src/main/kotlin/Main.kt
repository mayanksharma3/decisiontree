import com.andreapivetta.kolor.*
import java.io.File
import kotlin.math.log2

interface TreeNode
data class Node(val attribute: String, val children: List<TreeNode>): TreeNode
data class Leaf(val attribute: String): TreeNode

fun main() {
    val fileData = File("test.csv").readLines()
    val header = fileData.first().split(",").map { it.trim() }
    val data = fileData.drop(1).map { it.trim().split(",").map { c -> c.trim() } }
    println(decisionTree(header, data, "dataset", listOf()))
}

fun decisionTree(header: List<String>, data: List<List<String>>, nodeAttribute: String, attributesInBranch: List<String>): TreeNode {
    println("------------------------------------------------------------------------------------------------".green())
    println("Working out best node for branch ${attributesInBranch.joinToString(" ") { "$it -> " }} $nodeAttribute".green())
    val dataSetEntropy = entropy(data, nodeAttribute)
    val attributeAndGains = mutableMapOf<String, Double>()
    if(dataSetEntropy == -0.0) return Leaf(nodeAttribute)
    header.dropLast(1).filter { it !in attributesInBranch }.forEach {
        attributeAndGains[it] = informationGain(dataSetEntropy, it, header, data)
    }
    val bestAttribute = attributeAndGains.maxByOrNull { it.value }
    if(bestAttribute == null || bestAttribute.value == -0.0) {
        return Leaf(nodeAttribute)
    }
    println("Best attribute ${bestAttribute.key} with information gain ${bestAttribute.value}".green())
    println("------------------------------------------------------------------------------------------------".green())

    val children = data.groupBy { it[header.indexOf(bestAttribute.key)] }.map { decisionTree(header, it.value, it.key, attributesInBranch + bestAttribute.key) }
    return Node(bestAttribute.key, children)
}

private fun informationGain(datasetEntropy: Double, attribute: String, header: List<String>, data: List<List<String>>): Double {
    println("---------------------------------------------------------".blue())
    println("Calculating Information Gain for attribute $attribute".blue())
    val groupedOnAttribute = data.groupBy { it[header.indexOf(attribute)] }
    println("Data proportions for attribute $attribute")
    groupedOnAttribute.forEach { print("${it.key}: ${it.value.size}/${data.size} ") }
    print("\n")
    val igSummation = groupedOnAttribute.map { (it.value.size / data.size.toDouble()) * entropy(it.value, it.key) }.sum()
    val informationGain = datasetEntropy - igSummation
    println("Information Gain for $attribute: $informationGain".blue())
    println("---------------------------------------------------------".blue())
    return informationGain
}

private fun entropy(data: List<List<String>>, name: String): Double {
    println("----------------------------------".yellow())
    println("Calculating entropy for $name".yellow())
    val groupedData = data.groupBy { it.last() }.map { (it.value.size / data.size.toDouble()) }
    print("Data split: ")
    println(data.groupBy { it.last() }.map { it.key to "${it.value.size}/${data.size}" })
    print("Entropy: ".yellow())
    val entropy = groupedData.sumOf { it * log2(it) } * -1
    println(entropy.toString().yellow())
    println("----------------------------------".yellow())
    return entropy
}