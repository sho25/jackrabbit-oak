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
name|core
package|;
end_package

begin_comment
comment|/**  * An instances of this class represents a lazy value of type {@code T}.  * {@code LazyValue} implements an evaluate by need semantics:  * {@link #createValue()} is called exactly once when {@link #get()}  * is called for the first time.  *<p>  * {@code LazyValue} instances are thread safe.  */
end_comment

begin_class
specifier|abstract
class|class
name|LazyValue
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|volatile
name|T
name|value
decl_stmt|;
comment|/**      * Factory method called to create the value on an as need basis.      * @return a new instance for {@code T}.      */
specifier|protected
specifier|abstract
name|T
name|createValue
parameter_list|()
function_decl|;
comment|/**      * @return  {@code true} iff {@link #get()} has been called at least once.      */
specifier|public
name|boolean
name|hasValue
parameter_list|()
block|{
return|return
name|value
operator|!=
literal|null
return|;
block|}
comment|/**      * Get value. Calls {@link #createValue()} if called for the first time.      * @return  the value      */
specifier|public
name|T
name|get
parameter_list|()
block|{
comment|// Double checked locking is fine since Java 5 as long as value is volatile.
comment|// See http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|createValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

