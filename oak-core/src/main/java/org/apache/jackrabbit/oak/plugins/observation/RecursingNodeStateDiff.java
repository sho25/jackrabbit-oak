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
name|observation
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|DefaultNodeStateDiff
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A {@code RecursingNodeStateDiff} extends {@link DefaultNodeStateDiff}  * with a factory method for diffing child nodes.  * In contrast to {@code DefaultNodeStateDiff}, {@link #childNodeChanged(String, NodeState, NodeState)}  * should<em>not</em> recurse into child nodes but rather only be concerned about whether to continue  * diffing or not. The {@link #createChildDiff(String, NodeState, NodeState)} will be called instead  * for diffing child nodes.  * michid unify with NodeStateDiff  * michid move  */
end_comment

begin_class
specifier|public
class|class
name|RecursingNodeStateDiff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|public
specifier|static
specifier|final
name|RecursingNodeStateDiff
name|EMPTY
init|=
operator|new
name|RecursingNodeStateDiff
argument_list|()
decl_stmt|;
comment|/**      * Create a {@code RecursingNodeStateDiff} for a child node      * @param name  name of the child node      * @param before  before state of the child node      * @param after   after state of the child node      * @return  {@code RecursingNodeStateDiff} for the child node      */
annotation|@
name|Nonnull
specifier|public
name|RecursingNodeStateDiff
name|createChildDiff
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

