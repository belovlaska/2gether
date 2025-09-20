package ru.ifmo.is.together.common.search;

public enum SearchOperation {

  ANY, ALL, CONTAINS, DOES_NOT_CONTAIN, EQUAL, NOT_EQUAL,
  STR_EQUAL, STR_NOT_EQUAL, STR_CONTAINS, STR_DOES_NOT_CONTAIN,
  BEFORE, AFTER, BEGINS_WITH, DOES_NOT_BEGIN_WITH,
  ENDS_WITH, DOES_NOT_END_WITH, NUL, NOT_NULL,
  GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL;

  public static final String[] SIMPLE_OPERATION_SET = {
    "cn", "nc", "eq", "ne", "be", "af", "bw", "bn", "sc", "sd",
    "se", "sn", "ew", "en", "nu", "nn", "gt", "ge", "lt", "le" };

  public static SearchOperation getDataOption(final String dataOption) {
    return switch (dataOption.toLowerCase()) {
      case "all" -> ALL;
      case "any" -> ANY;
      default -> null;
    };
  }

  public static SearchOperation getSimpleOperation(final String input) {
    return switch (input.toLowerCase()) {
      case "cn" -> CONTAINS;
      case "nc" -> DOES_NOT_CONTAIN;
      case "sc" -> STR_CONTAINS;
      case "sd" -> STR_DOES_NOT_CONTAIN;
      case "eq" -> EQUAL;
      case "ne" -> NOT_EQUAL;
      case "se" -> STR_EQUAL;
      case "sn" -> STR_NOT_EQUAL;
      case "be" -> BEFORE;
      case "af" -> AFTER;
      case "bw" -> BEGINS_WITH;
      case "bn" -> DOES_NOT_BEGIN_WITH;
      case "ew" -> ENDS_WITH;
      case "en" -> DOES_NOT_END_WITH;
      case "nu" -> NUL;
      case "nn" -> NOT_NULL;
      case "gt" -> GREATER_THAN;
      case "ge" -> GREATER_THAN_EQUAL;
      case "lt" -> LESS_THAN;
      case "le" -> LESS_THAN_EQUAL;
      default -> null;
    };
  }
}
