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
name|index
operator|.
name|indexer
operator|.
name|document
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateEntry
block|{
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|public
name|NodeStateEntry
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|nodeState
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
block|}
end_class

end_unit
