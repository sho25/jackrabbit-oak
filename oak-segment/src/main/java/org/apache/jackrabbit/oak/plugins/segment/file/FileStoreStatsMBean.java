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
name|segment
operator|.
name|file
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
name|CompositeData
import|;
end_import

begin_interface
annotation|@
name|Deprecated
specifier|public
interface|interface
name|FileStoreStatsMBean
block|{
annotation|@
name|Deprecated
name|String
name|TYPE
init|=
literal|"FileStoreStats"
decl_stmt|;
annotation|@
name|Deprecated
name|long
name|getApproximateSize
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
name|int
name|getTarFileCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the writes to repository      */
annotation|@
name|Deprecated
name|CompositeData
name|getWriteStats
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the writes to repository      */
annotation|@
name|Deprecated
name|CompositeData
name|getRepositorySize
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
name|String
name|fileStoreInfoAsString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

