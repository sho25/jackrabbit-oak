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
name|mongomk
package|;
end_package

begin_comment
comment|/**  *<code>CollisionHandler</code>...  */
end_comment

begin_class
specifier|abstract
class|class
name|CollisionHandler
block|{
specifier|static
specifier|final
name|CollisionHandler
name|DEFAULT
init|=
operator|new
name|CollisionHandler
argument_list|()
block|{
annotation|@
name|Override
name|void
name|uncommittedModification
parameter_list|(
name|Revision
name|uncommitted
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
comment|/**      * Callback for an uncommitted modification in {@link Revision}      *<code>uncommitted</code>.      *      * @param uncommitted the uncommitted revision of the change.      */
specifier|abstract
name|void
name|uncommittedModification
parameter_list|(
name|Revision
name|uncommitted
parameter_list|)
function_decl|;
block|}
end_class

end_unit

