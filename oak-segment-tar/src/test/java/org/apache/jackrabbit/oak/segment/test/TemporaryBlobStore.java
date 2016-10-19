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
name|segment
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|DataStoreException
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
name|core
operator|.
name|data
operator|.
name|FileDataStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|TemporaryBlobStore
extends|extends
name|ExternalResource
block|{
specifier|private
specifier|final
name|TemporaryFolder
name|folder
decl_stmt|;
specifier|private
name|DataStoreBlobStore
name|store
decl_stmt|;
specifier|public
name|TemporaryBlobStore
parameter_list|(
name|TemporaryFolder
name|folder
parameter_list|)
block|{
name|this
operator|.
name|folder
operator|=
name|folder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
literal|4092
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
try|try
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|BlobStore
name|blobStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

