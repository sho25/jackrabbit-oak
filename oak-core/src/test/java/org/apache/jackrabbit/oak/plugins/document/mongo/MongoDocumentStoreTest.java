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
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|AbstractMongoConnectionTest
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
name|Collection
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
name|Document
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
name|DocumentMK
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
name|JournalEntry
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
name|MongoUtils
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
name|NodeDocument
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
name|mongo
operator|.
name|MongoUtils
operator|.
name|hasIndex
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  *<code>MongoDocumentStoreTest</code>...  */
end_comment

begin_class
specifier|public
class|class
name|MongoDocumentStoreTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|TestStore
name|store
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUpConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|store
operator|=
operator|new
name|TestStore
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|mk
operator|=
name|builder
operator|.
name|setMongoDB
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultIndexes
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|Document
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|SD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|SD_TYPE
argument_list|,
name|NodeDocument
operator|.
name|SD_MAX_REV_TIME_IN_SECS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
name|Document
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasIndex
argument_list|(
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|JOURNAL
argument_list|)
argument_list|,
name|JournalEntry
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|final
class|class
name|TestStore
extends|extends
name|MongoDocumentStore
block|{
name|TestStore
parameter_list|(
name|DB
name|db
parameter_list|,
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|db
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

