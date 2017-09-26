begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|mongo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoException
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_comment
comment|/**  * Provides static utility methods for MongoDB.  */
end_comment

begin_class
class|class
name|MongoUtils
block|{
comment|/**      * Forces creation of an index on a field, if one does not already exist.      *      * @param collection the collection.      * @param field the name of the field.      * @param ascending {@code true} for an ascending, {@code false} for a      *                              descending index.      * @param unique whether values are unique.      * @param sparse whether the index should be sparse.      * @throws MongoException if the operation fails.      */
specifier|static
name|void
name|createIndex
parameter_list|(
name|DBCollection
name|collection
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|ascending
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|boolean
name|sparse
parameter_list|)
throws|throws
name|MongoException
block|{
name|createIndex
argument_list|(
name|collection
argument_list|,
operator|new
name|String
index|[]
block|{
name|field
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
name|ascending
block|}
argument_list|,
name|unique
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
block|}
comment|/**      * Forces creation of an index on a set of fields, if one does not already      * exist.      *      * @param collection the collection.      * @param fields the name of the fields.      * @param ascending {@code true} for an ascending, {@code false} for a      *                              descending index.      * @param unique whether values are unique.      * @param sparse whether the index should be sparse.      * @throws IllegalArgumentException if {@code fields} and {@code ascending}      *          arrays have different lengths.      * @throws MongoException if the operation fails.      */
specifier|static
name|void
name|createIndex
parameter_list|(
name|DBCollection
name|collection
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|boolean
index|[]
name|ascending
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|boolean
name|sparse
parameter_list|)
throws|throws
name|MongoException
block|{
name|checkArgument
argument_list|(
name|fields
operator|.
name|length
operator|==
name|ascending
operator|.
name|length
argument_list|)
expr_stmt|;
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|index
operator|.
name|put
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|ascending
index|[
name|i
index|]
condition|?
literal|1
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|DBObject
name|options
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"unique"
argument_list|,
name|unique
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"sparse"
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
name|collection
operator|.
name|createIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**      * Forces creation of a partial index on a set of fields, if one does not      * already exist.      *      * @param collection the collection.      * @param fields the name of the fields.      * @param ascending {@code true} for an ascending, {@code false} for a      *                              descending index.      * @param filter the filter expression for the partial index.      * @throws MongoException if the operation fails.      */
specifier|static
name|void
name|createPartialIndex
parameter_list|(
name|DBCollection
name|collection
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|boolean
index|[]
name|ascending
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|MongoException
block|{
name|checkArgument
argument_list|(
name|fields
operator|.
name|length
operator|==
name|ascending
operator|.
name|length
argument_list|)
expr_stmt|;
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|index
operator|.
name|put
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|ascending
index|[
name|i
index|]
condition|?
literal|1
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|DBObject
name|options
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"partialFilterExpression"
argument_list|,
name|BasicDBObject
operator|.
name|parse
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|collection
operator|.
name|createIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns {@code true} if there is an index on the given fields,      * {@code false} otherwise. If multiple fields are passed, this method      * check if there a compound index on those field. This method does not      * check the sequence of fields for a compound index. That is, this method      * will return {@code true} as soon as it finds an index that covers the      * given fields, no matter their sequence in the compound index.      *      * @param collection the collection.      * @param fields the fields of an index.      * @return {@code true} if the index exists, {@code false} otherwise.      * @throws MongoException if the operation fails.      */
specifier|static
name|boolean
name|hasIndex
parameter_list|(
name|DBCollection
name|collection
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
throws|throws
name|MongoException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|uniqueFields
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|fields
argument_list|)
decl_stmt|;
for|for
control|(
name|DBObject
name|info
range|:
name|collection
operator|.
name|getIndexInfo
argument_list|()
control|)
block|{
name|DBObject
name|key
init|=
operator|(
name|DBObject
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexFields
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|key
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniqueFields
operator|.
name|equals
argument_list|(
name|indexFields
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit
