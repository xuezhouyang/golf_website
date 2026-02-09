#!/usr/bin/env kotlin

/**
 * Offline Invite Code Generator for SMS2Email
 * Flumenis LLC, Delaware
 *
 * Usage:
 *   kotlin InviteCodeGenerator.kt generate <code-name>
 *   kotlin InviteCodeGenerator.kt generate-batch <count>
 *   kotlin InviteCodeGenerator.kt generate-keys
 *
 * This script generates cryptographically signed invite codes that can be verified
 * offline by the app using RSA public key cryptography.
 *
 * Security:
 * - RSA 2048-bit key pair
 * - SHA256 with RSA signature
 * - Private key MUST be kept secure
 * - Codes are time-bound (365 days validity)
 */

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher

fun main(args: Array<String>) {
    when (args.getOrNull(0)) {
        "generate" -> {
            val codeName = args.getOrNull(1) ?: "PREMIUM"
            generateInviteCode(codeName)
        }
        "generate-batch" -> {
            val count = args.getOrNull(1)?.toIntOrNull() ?: 10
            generateBatchCodes(count)
        }
        "generate-keys" -> {
            generateKeyPair()
        }
        else -> {
            printUsage()
        }
    }
}

fun printUsage() {
    println("""
        |SMS2Email Invite Code Generator
        |===============================
        |
        |Usage:
        |  kotlin InviteCodeGenerator.kt generate <code-name>
        |      Generate a single invite code
        |
        |  kotlin InviteCodeGenerator.kt generate-batch <count>
        |      Generate multiple invite codes
        |
        |  kotlin InviteCodeGenerator.kt generate-keys
        |      Generate new RSA key pair
        |
        |Examples:
        |  kotlin InviteCodeGenerator.kt generate PROMO2025
        |  kotlin InviteCodeGenerator.kt generate-batch 100
        |
        |Note: Keep private.key file secure! Never share it.
    """.trimMargin())
}

/**
 * Generate RSA key pair
 */
fun generateKeyPair() {
    println("Generating RSA 2048-bit key pair...")

    val keyGen = KeyPairGenerator.getInstance("RSA")
    keyGen.initialize(2048)
    val keyPair = keyGen.generateKeyPair()

    // Save private key
    val privateKeyFile = java.io.File("private.key")
    privateKeyFile.writeBytes(keyPair.private.encoded)
    println("Private key saved to: private.key")
    println("⚠️  WARNING: Keep this file secure! Never share it!")

    // Save public key
    val publicKeyFile = java.io.File("public.key")
    publicKeyFile.writeBytes(keyPair.public.encoded)
    println("Public key saved to: public.key")

    // Print public key in Base64 format for app
    val publicKeyB64 = Base64.getEncoder().encodeToString(keyPair.public.encoded)
    println("\nPublic Key (Base64) - Add this to InviteCodeManager.kt:")
    println("=" * 70)
    // Split into lines of 64 characters for readability
    publicKeyB64.chunked(64).forEach { println(it) }
    println("=" * 70)
}

/**
 * Generate a single invite code
 */
fun generateInviteCode(codeName: String) {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    val today = dateFormat.format(Date())

    val message = "$codeName-$today"

    // Load private key
    val privateKey = loadPrivateKey()
    if (privateKey == null) {
        println("Error: private.key not found!")
        println("Run: kotlin InviteCodeGenerator.kt generate-keys")
        return
    }

    // Sign the message
    val signature = signMessage(message, privateKey)
    val signatureB64 = Base64.getEncoder().encodeToString(signature)
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")

    val inviteCode = "$message-$signatureB64"

    println("=" * 70)
    println("Invite Code Generated")
    println("=" * 70)
    println("Code:      $inviteCode")
    println("Name:      $codeName")
    println("Date:      $today")
    println("Valid for: 365 days")
    println("=" * 70)
}

/**
 * Generate batch of invite codes
 */
fun generateBatchCodes(count: Int) {
    println("Generating $count invite codes...")
    println()

    val privateKey = loadPrivateKey()
    if (privateKey == null) {
        println("Error: private.key not found!")
        return
    }

    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    val today = dateFormat.format(Date())

    val codes = mutableListOf<String>()

    repeat(count) { i ->
        val codeName = "CODE${String.format("%04d", i + 1)}"
        val message = "$codeName-$today"
        val signature = signMessage(message, privateKey)
        val signatureB64 = Base64.getEncoder().encodeToString(signature)
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")

        val inviteCode = "$message-$signatureB64"
        codes.add(inviteCode)
    }

    // Save to file
    val outputFile = java.io.File("invite_codes_$today.txt")
    outputFile.writeText(codes.joinToString("\n"))

    println("Generated $count codes")
    println("Saved to: ${outputFile.name}")
    println()
    println("Sample codes:")
    codes.take(5).forEach { println("  $it") }
    if (codes.size > 5) {
        println("  ... and ${codes.size - 5} more")
    }
}

/**
 * Sign a message using RSA private key
 */
fun signMessage(message: String, privateKey: PrivateKey): ByteArray {
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(privateKey)
    signature.update(message.toByteArray())
    return signature.sign()
}

/**
 * Load private key from file
 */
fun loadPrivateKey(): PrivateKey? {
    return try {
        val keyFile = java.io.File("private.key")
        if (!keyFile.exists()) return null

        val keyBytes = keyFile.readBytes()
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        keyFactory.generatePrivate(keySpec)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Utility extension
operator fun String.times(count: Int) = this.repeat(count)
