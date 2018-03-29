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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DuplicateKeyException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoCommandException
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
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoSocketException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ServerAddress
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcernException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
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
name|MongoConnectionFactory
import|;
end_import

begin_import
import|import
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
name|util
operator|.
name|MongoConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonInt32
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
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
name|DocumentStoreException
operator|.
name|Type
operator|.
name|GENERIC
import|;
end_import

begin_import
import|import static
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
name|DocumentStoreException
operator|.
name|Type
operator|.
name|TRANSIENT
import|;
end_import

begin_import
import|import static
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
name|MongoUtils
operator|.
name|isAvailable
import|;
end_import

begin_import
import|import static
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
operator|.
name|MongoUtils
operator|.
name|getDocumentStoreExceptionTypeFor
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_class
specifier|public
class|class
name|MongoUtilsTest
block|{
annotation|@
name|Rule
specifier|public
name|MongoConnectionFactory
name|connectionFactory
init|=
operator|new
name|MongoConnectionFactory
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|mongoAvailable
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createIndex
parameter_list|()
block|{
name|MongoConnection
name|c
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
name|MongoCollection
name|collection
init|=
name|c
operator|.
name|getDatabase
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MongoUtils
operator|.
name|createIndex
argument_list|(
name|collection
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MongoUtils
operator|.
name|createIndex
argument_list|(
name|collection
argument_list|,
literal|"bar"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MongoUtils
operator|.
name|createIndex
argument_list|(
name|collection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"baz"
block|,
literal|"qux"
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|false
block|}
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"baz"
argument_list|,
literal|"qux"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|indexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|collection
operator|.
name|listIndexes
argument_list|()
operator|.
name|into
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|indexes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Document
name|info
range|:
name|indexes
control|)
block|{
name|Document
name|key
init|=
operator|(
name|Document
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|info
operator|.
name|get
argument_list|(
literal|"sparse"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|info
operator|.
name|get
argument_list|(
literal|"unique"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"baz"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"qux"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createPartialIndex
parameter_list|()
block|{
name|MongoConnection
name|c
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
name|MongoStatus
name|status
init|=
operator|new
name|MongoStatus
argument_list|(
name|c
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|c
operator|.
name|getDBName
argument_list|()
argument_list|)
decl_stmt|;
name|assumeTrue
argument_list|(
name|status
operator|.
name|isVersion
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MongoCollection
name|collection
init|=
name|c
operator|.
name|getDatabase
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MongoUtils
operator|.
name|createPartialIndex
argument_list|(
name|collection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|true
block|}
argument_list|,
literal|"{foo:true}"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoUtils
operator|.
name|hasIndex
argument_list|(
name|collection
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|indexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|collection
operator|.
name|listIndexes
argument_list|()
operator|.
name|into
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|indexes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Document
name|info
range|:
name|indexes
control|)
block|{
name|Document
name|key
init|=
operator|(
name|Document
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|key
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|filter
init|=
operator|(
name|Document
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"partialFilterExpression"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|filter
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|checkArguments
parameter_list|()
block|{
name|MongoConnection
name|c
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|MongoCollection
name|collection
init|=
name|c
operator|.
name|getDatabase
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MongoUtils
operator|.
name|createIndex
argument_list|(
name|collection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
literal|true
block|}
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|documentStoreExceptionType
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|GENERIC
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GENERIC
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
operator|new
name|MongoException
argument_list|(
literal|"message"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GENERIC
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newMongoCommandException
argument_list|(
literal|42
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GENERIC
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
operator|new
name|DuplicateKeyException
argument_list|(
name|response
argument_list|(
literal|11000
argument_list|)
argument_list|,
operator|new
name|ServerAddress
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newWriteConcernException
argument_list|(
literal|11600
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newWriteConcernException
argument_list|(
literal|11601
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newWriteConcernException
argument_list|(
literal|11602
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newMongoCommandException
argument_list|(
literal|11600
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newMongoCommandException
argument_list|(
literal|11601
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
name|newMongoCommandException
argument_list|(
literal|11602
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TRANSIENT
argument_list|,
name|getDocumentStoreExceptionTypeFor
argument_list|(
operator|new
name|MongoSocketException
argument_list|(
literal|"message"
argument_list|,
operator|new
name|ServerAddress
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|MongoCommandException
name|newMongoCommandException
parameter_list|(
name|int
name|code
parameter_list|)
block|{
return|return
operator|new
name|MongoCommandException
argument_list|(
name|response
argument_list|(
name|code
argument_list|)
argument_list|,
operator|new
name|ServerAddress
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|WriteConcernException
name|newWriteConcernException
parameter_list|(
name|int
name|code
parameter_list|)
block|{
return|return
operator|new
name|WriteConcernException
argument_list|(
name|response
argument_list|(
name|code
argument_list|)
argument_list|,
operator|new
name|ServerAddress
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|BsonDocument
name|response
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|BsonDocument
name|response
init|=
operator|new
name|BsonDocument
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"code"
argument_list|,
operator|new
name|BsonInt32
argument_list|(
name|code
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"errmsg"
argument_list|,
operator|new
name|BsonString
argument_list|(
literal|"message"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

