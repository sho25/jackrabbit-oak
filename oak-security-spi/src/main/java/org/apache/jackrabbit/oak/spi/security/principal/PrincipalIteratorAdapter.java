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
name|spi
operator|.
name|security
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|principal
operator|.
name|PrincipalIterator
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
name|commons
operator|.
name|iterator
operator|.
name|RangeIteratorAdapter
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
name|commons
operator|.
name|iterator
operator|.
name|RangeIteratorDecorator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Principal specific {@code RangeIteratorAdapter} implementing the  * {@code PrincipalIterator} interface.  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalIteratorAdapter
extends|extends
name|RangeIteratorDecorator
implements|implements
name|PrincipalIterator
block|{
comment|/**      * Static instance of an empty {@link PrincipalIterator}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
specifier|final
name|PrincipalIteratorAdapter
name|EMPTY
init|=
operator|new
name|PrincipalIteratorAdapter
argument_list|(
operator|(
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
operator|)
name|RangeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|/**      * Creates an adapter for the given {@link java.util.Iterator} of principals.      *      * @param iterator iterator of {@link java.security.Principal}s      */
specifier|public
name|PrincipalIteratorAdapter
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|iterator
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|RangeIteratorAdapter
argument_list|(
name|iterator
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an iterator for the given collection of {@code Principal}s.      *      * @param collection collection of {@link Principal} objects.      */
specifier|public
name|PrincipalIteratorAdapter
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|collection
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|RangeIteratorAdapter
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------< AccessControlPolicyIterator>---
comment|/**      * Returns the next policy.      *      * @return next policy.      */
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Principal
name|nextPrincipal
parameter_list|()
block|{
return|return
operator|(
name|Principal
operator|)
name|next
argument_list|()
return|;
block|}
block|}
end_class

end_unit

