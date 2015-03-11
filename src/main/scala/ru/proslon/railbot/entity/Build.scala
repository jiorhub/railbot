package ru.proslon.railbot.entity

import com.fasterxml.jackson.annotation.{JsonProperty, JsonTypeInfo}

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:01
 */

/*
"Body":[
  {"type":"0","userID":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","level":"6","buildDateTime":"-284573",
  "durationLeft":"9936","lastDurationUpdate":"-124377","lastProductionUpdate":"-284573","productionTimeLeft":"0",
  "hasUpgraded":"1","costOfLastUpgrade":"400000"},
  */

case class Build(
  @JsonProperty("type")
  Type: String,
  userID: String,
  level: String,
  buildDateTime: String,
  durationLeft: String,
  lastDurationUpdate: String,
  lastProductionUpdate: String,
  productionTimeLeft: String,
  hasUpgraded: String,
  costOfLastUpgrade: String
) {}
