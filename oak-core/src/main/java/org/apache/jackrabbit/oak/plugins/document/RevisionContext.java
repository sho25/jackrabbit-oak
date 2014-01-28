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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Provides revision related context.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RevisionContext
block|{
comment|/**      * @return the branches of the local MongoMK instance, which are not yet      *         merged.      */
name|UnmergedBranches
name|getBranches
parameter_list|()
function_decl|;
comment|/**      * @return the pending modifications.      */
name|UnsavedModifications
name|getPendingModifications
parameter_list|()
function_decl|;
comment|/**      * @return the revision comparator.      */
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|getRevisionComparator
parameter_list|()
function_decl|;
comment|/**      * @return the cluster id of the local MongoMK instance.      */
name|int
name|getClusterId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

