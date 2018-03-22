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
name|segment
operator|.
name|spi
operator|.
name|persistence
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Manifest is a properties files, providing the information about the segment  * store (eg. the schema version number).  *<p>  * The implementation<b>doesn't need to be</b> thread-safe.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ManifestFile
block|{
comment|/**      * Check if the manifest already exists.      * @return {@code true} if the manifest exists      */
name|boolean
name|exists
parameter_list|()
function_decl|;
comment|/**      * Load the properties from the manifest file.      * @return properties describing the segmentstore      * @throws IOException      */
name|Properties
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Store the properties to the manifest file.      * @param properties describing the segmentstore      * @throws IOException      */
name|void
name|save
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

