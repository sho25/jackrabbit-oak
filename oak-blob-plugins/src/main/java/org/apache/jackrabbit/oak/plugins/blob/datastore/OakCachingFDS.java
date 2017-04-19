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
name|plugins
operator|.
name|blob
operator|.
name|datastore
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|CachingFDS
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
name|DataStoreException
import|;
end_import

begin_comment
comment|/**  * Overrides the implementation of  * {@link org.apache.jackrabbit.core.data.CachingDataStore#getOrCreateReferenceKey}.  */
end_comment

begin_class
specifier|public
class|class
name|OakCachingFDS
extends|extends
name|CachingFDS
block|{
comment|/** The path for FS Backend **/
specifier|private
name|String
name|fsBackendPath
decl_stmt|;
specifier|public
name|void
name|setFsBackendPath
parameter_list|(
name|String
name|fsBackendPath
parameter_list|)
block|{
name|this
operator|.
name|fsBackendPath
operator|=
name|fsBackendPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|getOrCreateReferenceKey
parameter_list|()
throws|throws
name|DataStoreException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fsBackendPath
argument_list|,
literal|"reference.key"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|FileUtils
operator|.
name|readFileToByteArray
argument_list|(
name|file
argument_list|)
return|;
block|}
else|else
block|{
name|byte
index|[]
name|key
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
operator|new
name|SecureRandom
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeByteArrayToFile
argument_list|(
name|file
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Unable to access reference key file "
operator|+
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
