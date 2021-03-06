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
name|upgrade
operator|.
name|cli
operator|.
name|container
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|MongoFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
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
name|MongoNodeStoreContainer
implements|implements
name|NodeStoreContainer
block|{
specifier|private
specifier|static
name|Boolean
name|mongoAvailable
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MongoNodeStoreContainer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MONGO_URI
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.mongo.uri"
argument_list|,
literal|"mongodb://localhost:27017/oak-migration"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|DATABASE_SUFFIX
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MongoFactory
name|mongoFactory
decl_stmt|;
specifier|private
specifier|final
name|BlobStoreContainer
name|blob
decl_stmt|;
specifier|private
specifier|final
name|String
name|mongoUri
decl_stmt|;
specifier|private
name|Closer
name|closer
decl_stmt|;
specifier|public
name|MongoNodeStoreContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|DummyBlobStoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MongoNodeStoreContainer
parameter_list|(
name|BlobStoreContainer
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|isMongoAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|mongoUri
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s-%d"
argument_list|,
name|MONGO_URI
argument_list|,
name|DATABASE_SUFFIX
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|mongoFactory
operator|=
operator|new
name|MongoFactory
argument_list|(
name|mongoUri
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|blob
operator|=
name|blob
expr_stmt|;
name|clean
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isMongoAvailable
parameter_list|()
block|{
if|if
condition|(
name|mongoAvailable
operator|!=
literal|null
condition|)
block|{
return|return
name|mongoAvailable
return|;
block|}
name|mongoAvailable
operator|=
name|testMongoAvailability
argument_list|()
expr_stmt|;
return|return
name|mongoAvailable
return|;
block|}
specifier|private
specifier|static
name|boolean
name|testMongoAvailability
parameter_list|()
block|{
name|Mongo
name|mongo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MongoClientURI
name|uri
init|=
operator|new
name|MongoClientURI
argument_list|(
name|MONGO_URI
operator|+
literal|"?connectTimeoutMS=3000"
argument_list|)
decl_stmt|;
name|mongo
operator|=
operator|new
name|MongoClient
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|mongo
operator|.
name|getDatabaseNames
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|mongo
operator|!=
literal|null
condition|)
block|{
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|closer
operator|=
name|Closer
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|mongoFactory
operator|.
name|create
argument_list|(
name|blob
operator|.
name|open
argument_list|()
argument_list|,
name|closer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't close document node store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clean
parameter_list|()
throws|throws
name|IOException
block|{
name|MongoClientURI
name|uri
init|=
operator|new
name|MongoClientURI
argument_list|(
name|mongoUri
argument_list|)
decl_stmt|;
name|MongoClient
name|client
init|=
operator|new
name|MongoClient
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|client
operator|.
name|dropDatabase
argument_list|(
name|uri
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
name|blob
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|mongoUri
return|;
block|}
block|}
end_class

end_unit

