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

begin_comment
comment|/**  * Interface to be implemented by a shared data store.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SharedDataStore
block|{
comment|/**      * Explicitly identifies the type of the data store      */
enum|enum
name|Type
block|{
name|SHARED
block|,
name|DEFAULT
block|}
comment|/**      * Adds the root record.      *       * @param stream the stream      * @param name the name of the root record      * @return the data record      * @throws DataStoreException the data store exception      */
name|void
name|addMetadataRecord
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Retrieves the metadata record with the given name      *      * @param name the name of the record      * @return      */
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Gets the all root records.      *       * @return the all root records      */
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
comment|/**      * Deletes the root record represented by the given parameters.      *       * @param name the name of the root record      * @return success/failure      */
name|boolean
name|deleteMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Deletes all records matching the given prefix.      *       * @param prefix metadata type identifier      */
name|void
name|deleteAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
comment|/**      * Gets the type.      *       * @return the type      */
name|Type
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

