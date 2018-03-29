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
name|document
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|io
operator|.
name|Closer
import|;
end_import

begin_class
specifier|public
class|class
name|CloseableIterable
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|T
argument_list|>
implements|,
name|Closeable
block|{
specifier|private
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CloseableIterable
argument_list|<
name|T
argument_list|>
name|wrap
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|,
name|Closeable
name|closeable
parameter_list|)
block|{
return|return
operator|new
name|CloseableIterable
argument_list|<
name|T
argument_list|>
argument_list|(
name|iterable
argument_list|,
name|closeable
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CloseableIterable
argument_list|<
name|T
argument_list|>
name|wrap
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
operator|new
name|CloseableIterable
argument_list|<
name|T
argument_list|>
argument_list|(
name|iterable
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|CloseableIterable
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|,
name|Closeable
name|closeable
parameter_list|)
block|{
name|this
operator|.
name|iterable
operator|=
name|iterable
expr_stmt|;
if|if
condition|(
name|closeable
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|closer
operator|.
name|register
argument_list|(
name|closeable
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|instanceof
name|Closeable
condition|)
block|{
name|closer
operator|.
name|register
argument_list|(
operator|(
name|Closeable
operator|)
name|it
argument_list|)
expr_stmt|;
block|}
return|return
name|it
return|;
block|}
block|}
end_class

end_unit

