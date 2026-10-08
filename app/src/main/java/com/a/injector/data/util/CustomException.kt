package com.a.injector.data.util

class ManageStoragePermissionDenied: Exception("")
class ManageStorageMethodFailed: Exception("")

class ShizukuUnauthorized: Exception()
class ShizukuMethodFailed(val exitCode: String): Exception(exitCode)

class SuperuserDenied: Exception()
class SuperuserMethodFailed(val exitCode: String): Exception(exitCode)