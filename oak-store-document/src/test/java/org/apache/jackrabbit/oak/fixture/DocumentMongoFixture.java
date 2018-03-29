begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|fixture
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|DocumentNodeStore
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientURI
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentMongoFixture
extends|extends
name|NodeStoreFixture
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentMongoFixture
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeStore
argument_list|,
name|String
argument_list|>
name|suffixes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|NodeStore
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Boolean
name|isAvailable
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|sequence
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
name|DocumentMongoFixture
parameter_list|(
name|String
name|uri
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
block|}
specifier|public
name|DocumentMongoFixture
parameter_list|()
block|{
name|this
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
try|try
block|{
name|String
name|suffix
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%d-%d"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|sequence
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache,time"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMongoDB
argument_list|(
name|createClient
argument_list|()
argument_list|,
name|getDBName
argument_list|(
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|ns
init|=
name|builder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|suffixes
operator|.
name|put
argument_list|(
name|ns
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
return|return
name|ns
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"Mongo instance is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|MongoClient
name|createClient
parameter_list|()
block|{
return|return
operator|new
name|MongoClient
argument_list|(
operator|new
name|MongoClientURI
argument_list|(
name|uri
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getDBName
parameter_list|(
name|String
name|suffix
parameter_list|)
block|{
name|String
name|dbName
init|=
operator|new
name|MongoClientURI
argument_list|(
name|uri
argument_list|)
operator|.
name|getDatabase
argument_list|()
decl_stmt|;
return|return
name|dbName
operator|+
literal|"-"
operator|+
name|suffix
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
if|if
condition|(
name|isAvailable
operator|==
literal|null
condition|)
block|{
name|isAvailable
operator|=
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
expr_stmt|;
block|}
return|return
name|isAvailable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
if|if
condition|(
name|nodeStore
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
operator|(
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|suffix
init|=
name|suffixes
operator|.
name|remove
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffix
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|MongoClient
name|client
init|=
name|createClient
argument_list|()
init|)
block|{
name|client
operator|.
name|dropDatabase
argument_list|(
name|getDBName
argument_list|(
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't close Mongo"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DocumentNodeStore[Mongo] on "
operator|+
name|this
operator|.
name|uri
return|;
block|}
block|}
end_class

end_unit

