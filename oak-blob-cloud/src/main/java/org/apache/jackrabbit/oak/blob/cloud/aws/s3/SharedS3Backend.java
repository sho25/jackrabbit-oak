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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
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
name|Backend
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
name|DataRecord
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_comment
comment|/**  * Extension to the {@link org.apache.jackrabbit.core.data.Backend} for supporting adding meta data to the underlying  * store.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SharedS3Backend
extends|extends
name|Backend
block|{
comment|/**      * Adds a metadata record with the specified name      *      * @param input the record input stream      * @param name the name      * @throws org.apache.jackrabbit.core.data.DataStoreException      */
name|void
name|addMetadataRecord
parameter_list|(
specifier|final
name|InputStream
name|input
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Gets the metadata of the specified name.      *      * @param name the name of the record      * @return the metadata DataRecord      */
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Gets all the metadata with a specified prefix.      *      * @param prefix the prefix of the records to retrieve      * @return list of all the metadata DataRecords      */
name|List
argument_list|<
name|DataRecord
argument_list|>
name|getAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
comment|/**      * Deletes the metadata record with the specified name      *      * @param name the name of the record      * @return boolean to indicate success of deletion      */
name|boolean
name|deleteMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Deletes all the metadata records with the specified prefix.      *      * @param prefix the prefix of the record      */
name|void
name|deleteAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

