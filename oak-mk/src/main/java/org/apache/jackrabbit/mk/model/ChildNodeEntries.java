begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
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
name|mk
operator|.
name|store
operator|.
name|Binding
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ChildNodeEntries
extends|extends
name|Cloneable
block|{
specifier|static
specifier|final
name|int
name|CAPACITY_THRESHOLD
init|=
literal|10000
decl_stmt|;
name|Object
name|clone
parameter_list|()
function_decl|;
name|boolean
name|inlined
parameter_list|()
function_decl|;
comment|//-------------------------------------------------------------< read ops>
name|int
name|getCount
parameter_list|()
function_decl|;
name|ChildNode
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|getEntries
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|//------------------------------------------------------------< write ops>
name|ChildNode
name|add
parameter_list|(
name|ChildNode
name|entry
parameter_list|)
function_decl|;
name|ChildNode
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|ChildNode
name|rename
parameter_list|(
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
function_decl|;
comment|//-------------------------------------------------------------< diff ops>
comment|/**      * Returns those entries that exist in<code>other</code> but not in      *<code>this</code>.      *      * @param other      * @return      */
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|getAdded
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
function_decl|;
comment|/**      * Returns those entries that exist in<code>this</code> but not in      *<code>other</code>.      *      * @param other      * @return      */
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|getRemoved
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
function_decl|;
comment|/**      * Returns<code>this</code> instance's entries that have namesakes in      *<code>other</code> but with different<code>id</code>s.      *      * @param other      * @return      */
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|getModified
parameter_list|(
specifier|final
name|ChildNodeEntries
name|other
parameter_list|)
function_decl|;
comment|//------------------------------------------------< serialization support>
name|void
name|serialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

