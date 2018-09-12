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
name|spi
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
name|File
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
name|Iterator
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
name|DataIdentifier
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
comment|/**  */
end_comment

begin_interface
specifier|public
interface|interface
name|SharedBackend
block|{
comment|/**      * Return inputstream of record identified by identifier.      *      * @param identifier      *            identifier of record.      * @return inputstream of the record.      * @throws DataStoreException      *             if record not found or any error.      */
name|InputStream
name|read
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Stores file to backend with identifier used as key. If key pre-exists, it      * updates the timestamp of the key.      *      * @param identifier      *            key of the file      * @param file      *            file that would be stored in backend.      * @throws DataStoreException      *             for any error.      */
name|void
name|write
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Gets the record with the specified identifier      *      * @param id the record identifier      * @return the metadata DataRecord      */
name|DataRecord
name|getRecord
parameter_list|(
name|DataIdentifier
name|id
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Returns identifiers of all records that exists in backend.      *      * @return iterator consisting of all identifiers      * @throws DataStoreException      */
name|Iterator
argument_list|<
name|DataIdentifier
argument_list|>
name|getAllIdentifiers
parameter_list|()
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Returns a list of all DataRecords      *      * @return iterator over DataRecords      * @throws DataStoreException      */
name|Iterator
argument_list|<
name|DataRecord
argument_list|>
name|getAllRecords
parameter_list|()
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * This method check the existence of record in backend.      *      * @param identifier      *            identifier to be checked.      * @return true if records exists else false.      * @throws DataStoreException      */
name|boolean
name|exists
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Close backend and release resources like database connection if any.      *      * @throws DataStoreException      */
name|void
name|close
parameter_list|()
throws|throws
name|DataStoreException
function_decl|;
comment|/**      * Delete record identified by identifier. No-op if identifier not found.      *      * @param identifier      * @throws DataStoreException      */
name|void
name|deleteRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
function_decl|;
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
comment|/**      * Adds a metadata record with the specified name      *      * @param input the record file      * @param name the name      * @throws org.apache.jackrabbit.core.data.DataStoreException      */
name|void
name|addMetadataRecord
parameter_list|(
specifier|final
name|File
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
comment|/**      * Checks if the metadata record with the specified name exists.      *      * @param name the name of the record      * @return whether record exists      */
name|boolean
name|metadataRecordExists
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Initialize      */
name|void
name|init
parameter_list|()
throws|throws
name|DataStoreException
function_decl|;
block|}
end_interface

end_unit

