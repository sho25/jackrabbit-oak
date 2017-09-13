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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|Oak
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
import|;
end_import

begin_comment
comment|/**  * A repository stub implementation for Oak Segment Tar  */
end_comment

begin_class
specifier|public
class|class
name|OakSegmentTarRepositoryStub
extends|extends
name|OakRepositoryStub
block|{
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
comment|/**      * Constructor as required by the JCR TCK.      *      * @param settings repository settings      * @throws RepositoryException If an error occurs.      */
specifier|public
name|OakSegmentTarRepositoryStub
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
try|try
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"segment-tar-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|store
operator|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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

