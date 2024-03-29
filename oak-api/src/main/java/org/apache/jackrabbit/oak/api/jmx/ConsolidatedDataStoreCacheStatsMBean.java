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
name|api
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ConsolidatedDataStoreCacheStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"ConsolidatedDataStoreCacheStats"
decl_stmt|;
name|TabularData
name|getCacheStats
parameter_list|()
function_decl|;
comment|/**      * Determines whether a file-like entity with the given name      * has been "synced" (completely copied) to S3.      *      * @param nodePathName - Path to the entity to check.  This is      *                       the repository node path, not an external file path.      * @return true if the file is synced to S3.      */
name|boolean
name|isFileSynced
parameter_list|(
specifier|final
name|String
name|nodePathName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

