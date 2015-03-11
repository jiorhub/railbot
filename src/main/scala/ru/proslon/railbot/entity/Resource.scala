package ru.proslon.railbot.entity

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:15
 */


//{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"0","amount":"166117","limit":"800000"}

case class Resource(
 userId: String,
 resourceId: String,
 amount: String,
 limit: String
) {}
