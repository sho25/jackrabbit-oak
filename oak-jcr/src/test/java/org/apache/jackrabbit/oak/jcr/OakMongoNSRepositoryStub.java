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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|MongoConnection
import|;
end_import

begin_comment
comment|/**  * A repository stub using the DocumentNodeStore.  */
end_comment

begin_class
specifier|public
class|class
name|OakMongoNSRepositoryStub
extends|extends
name|OakRepositoryStub
block|{
static|static
block|{
name|MongoConnection
name|c
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|MongoConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
comment|/**      * Constructor as required by the JCR TCK.      *      * @param settings repository settings      * @throws javax.jcr.RepositoryException If an error occurs.      */
specifier|public
name|OakMongoNSRepositoryStub
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
try|try
block|{
name|this
operator|.
name|connection
operator|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|memoryCacheSize
argument_list|(
literal|64
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache,time"
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|connection
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|preCreateRepository
argument_list|(
name|jcr
argument_list|)
expr_stmt|;
name|this
operator|.
name|repository
operator|=
name|jcr
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|loadTestContent
argument_list|(
name|repository
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isMongoDBAvailable
parameter_list|()
block|{
return|return
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
return|;
block|}
specifier|static
name|MongoConnection
name|createConnection
parameter_list|(
name|String
name|db
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|MongoUtils
operator|.
name|getConnection
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/**      * Returns the configured repository instance.      *      * @return the configured repository instance.      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
block|}
end_class

end_unit

