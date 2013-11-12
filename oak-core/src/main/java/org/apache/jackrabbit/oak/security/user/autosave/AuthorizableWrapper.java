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
name|security
operator|.
name|user
operator|.
name|autosave
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Group
import|;
end_import

begin_class
specifier|final
class|class
name|AuthorizableWrapper
parameter_list|<
name|T
extends|extends
name|Authorizable
parameter_list|>
implements|implements
name|Function
argument_list|<
name|T
argument_list|,
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|AutoSaveEnabledManager
name|mgr
decl_stmt|;
specifier|private
name|AuthorizableWrapper
parameter_list|(
name|AutoSaveEnabledManager
name|mgr
parameter_list|)
block|{
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|apply
parameter_list|(
name|T
name|authorizable
parameter_list|)
block|{
if|if
condition|(
name|authorizable
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
name|T
operator|)
name|mgr
operator|.
name|wrap
argument_list|(
name|authorizable
argument_list|)
return|;
block|}
block|}
specifier|static
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|createIterator
parameter_list|(
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|dlgs
parameter_list|,
name|AutoSaveEnabledManager
name|mgr
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|dlgs
argument_list|,
operator|new
name|AuthorizableWrapper
argument_list|(
name|mgr
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|Iterator
argument_list|<
name|Group
argument_list|>
name|createGroupIterator
parameter_list|(
name|Iterator
argument_list|<
name|Group
argument_list|>
name|dlgs
parameter_list|,
name|AutoSaveEnabledManager
name|mgr
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|dlgs
argument_list|,
operator|new
name|AuthorizableWrapper
argument_list|(
name|mgr
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

