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
name|blob
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
name|mk
operator|.
name|blobs
operator|.
name|BlobStore
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
name|base
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Interface for building blob stores.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobStoreBuilder
block|{
comment|/**      * Builds the appropriate BlobStore.      *       * @param config      *            the config      * @return the blob store wrapped as {@link Optional} to indicate that the      *         value might be null      * @throws Exception      *             the exception      */
specifier|public
name|Optional
argument_list|<
name|BlobStore
argument_list|>
name|build
parameter_list|(
name|BlobStoreConfiguration
name|config
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

