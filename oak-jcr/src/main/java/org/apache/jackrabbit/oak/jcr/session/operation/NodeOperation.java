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
operator|.
name|session
operator|.
name|operation
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
name|jcr
operator|.
name|delegate
operator|.
name|NodeDelegate
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|NodeOperation
parameter_list|<
name|U
parameter_list|>
extends|extends
name|ItemOperation
argument_list|<
name|U
argument_list|>
block|{
specifier|protected
specifier|final
name|NodeDelegate
name|node
decl_stmt|;
specifier|protected
name|NodeOperation
parameter_list|(
name|NodeDelegate
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
block|}
end_class

end_unit

