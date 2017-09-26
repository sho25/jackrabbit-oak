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
name|plugins
operator|.
name|document
operator|.
name|persistentCache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|cache
operator|.
name|CacheValue
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
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStore
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
name|plugins
operator|.
name|document
operator|.
name|DocumentStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|WriteBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|type
operator|.
name|DataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|type
operator|.
name|StringDataType
import|;
end_import

begin_class
specifier|public
class|class
name|ValueDataType
implements|implements
name|DataType
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|docNodeStore
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|docStore
decl_stmt|;
specifier|private
specifier|final
name|CacheType
name|type
decl_stmt|;
specifier|public
name|ValueDataType
parameter_list|(
name|DocumentNodeStore
name|docNodeStore
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|CacheType
name|type
parameter_list|)
block|{
name|this
operator|.
name|docNodeStore
operator|=
name|docNodeStore
expr_stmt|;
name|this
operator|.
name|docStore
operator|=
name|docStore
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
operator|(
name|CacheValue
operator|)
name|obj
operator|)
operator|.
name|getMemory
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|WriteBuffer
name|buff
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|String
name|s
init|=
name|type
operator|.
name|valueToString
argument_list|(
name|obj
argument_list|)
decl_stmt|;
name|StringDataType
operator|.
name|INSTANCE
operator|.
name|write
argument_list|(
name|buff
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|read
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
block|{
name|String
name|s
init|=
name|StringDataType
operator|.
name|INSTANCE
operator|.
name|read
argument_list|(
name|buff
argument_list|)
decl_stmt|;
return|return
name|type
operator|.
name|valueFromString
argument_list|(
name|docNodeStore
argument_list|,
name|docStore
argument_list|,
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|WriteBuffer
name|buff
parameter_list|,
name|Object
index|[]
name|obj
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|write
argument_list|(
name|buff
argument_list|,
name|obj
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|read
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|,
name|Object
index|[]
name|obj
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|obj
index|[
name|i
index|]
operator|=
name|read
argument_list|(
name|buff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
