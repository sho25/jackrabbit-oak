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
name|jcr
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
name|oak
operator|.
name|api
operator|.
name|Tree
operator|.
name|Status
import|;
end_import

begin_comment
comment|/**  * Abstract base class for {@link NodeDelegate} and {@link PropertyDelegate}  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ItemDelegate
block|{
specifier|protected
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|protected
name|ItemDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
block|}
comment|/**      * Get the name of this item      * @return oak name of this item      */
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Get the path of this item      * @return oak path of this item      */
specifier|abstract
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Get the parent of this item      * @return  parent of this item or {@code null} for root      */
specifier|abstract
name|NodeDelegate
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Determine whether this item is stale      * @return  {@code true} iff stale      */
specifier|abstract
name|boolean
name|isStale
parameter_list|()
function_decl|;
comment|/**      * Get the status of this item      * @return  {@link Status} of this item      */
specifier|abstract
name|Status
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * Get the session delegate with which this item is associated      * @return  {@link SessionDelegate} to which this item belongs      */
specifier|abstract
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
function_decl|;
block|}
end_class

end_unit

