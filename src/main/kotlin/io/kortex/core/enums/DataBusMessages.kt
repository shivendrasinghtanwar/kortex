package io.kortex.core.enums

enum class DataBusMessages {
  FindById,
  Find,
  Insert,
  Delete,
  DeleteById,
  Update,
  UpdateById,
  UpdateMultipleByIds,
  Count;

  fun from(collectionName: CollectionNames):String{
    return "db.$collectionName.$this"
  }
  fun into(collectionName: CollectionNames):String{
    return "db.$collectionName.$this"
  }
}
