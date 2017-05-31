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
name|run
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ImmutableSet
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
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_class
class|class
name|ReadOnlyBlobStoreWrapper
block|{
specifier|public
specifier|static
name|BlobStore
name|wrap
parameter_list|(
name|BlobStore
name|delegate
parameter_list|)
block|{
name|Class
index|[]
name|interfaces
init|=
name|delegate
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
decl_stmt|;
return|return
operator|(
name|BlobStore
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|ReadOnlyBlobStoreWrapper
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|interfaces
argument_list|,
operator|new
name|ReadOnlyHandler
argument_list|(
name|delegate
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|ReadOnlyHandler
implements|implements
name|InvocationHandler
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|writableMethods
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"writeBlob"
argument_list|,
comment|//BlobStore
literal|"deleteChunks"
argument_list|,
comment|//GarbageCollectableBlobStore
literal|"countDeleteChunks"
comment|//GarbageCollectableBlobStore
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|delegate
decl_stmt|;
name|ReadOnlyHandler
parameter_list|(
name|BlobStore
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
name|methodName
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|writableMethods
operator|.
name|contains
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Readonly BlobStore - Cannot invoked "
operator|+
name|method
argument_list|)
throw|;
block|}
return|return
name|method
operator|.
name|invoke
argument_list|(
name|delegate
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

