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
name|document
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

begin_comment
comment|/**  * MBean exposing DocumentNodeStore revision garbage collection statistics.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RevisionGCStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"RevisionGCStats"
decl_stmt|;
name|long
name|getReadDocCount
parameter_list|()
function_decl|;
name|long
name|getDeletedDocCount
parameter_list|()
function_decl|;
name|long
name|getDeletedLeafDocCount
parameter_list|()
function_decl|;
name|long
name|getDeletedSplitDocCount
parameter_list|()
function_decl|;
name|long
name|getDeletedIntSplitDocCount
parameter_list|()
function_decl|;
name|long
name|getResetDeletedFlagCount
parameter_list|()
function_decl|;
name|CompositeData
name|getReadDocHistory
parameter_list|()
function_decl|;
name|CompositeData
name|getDeletedDocHistory
parameter_list|()
function_decl|;
name|CompositeData
name|getDeletedLeafDocHistory
parameter_list|()
function_decl|;
name|CompositeData
name|getDeletedSplitDocHistory
parameter_list|()
function_decl|;
name|CompositeData
name|getDeletedIntSplitDocHistory
parameter_list|()
function_decl|;
name|CompositeData
name|getResetDeletedFlagHistory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
