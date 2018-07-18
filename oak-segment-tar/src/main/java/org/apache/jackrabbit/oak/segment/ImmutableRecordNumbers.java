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
name|segment
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
name|collect
operator|.
name|AbstractIterator
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
comment|/**  * An immutable record table. It is initialized at construction time and can  * never be changed afterwards.  *<p>  * This implementation is trivially thread-safe.  */
end_comment

begin_class
class|class
name|ImmutableRecordNumbers
implements|implements
name|RecordNumbers
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|byte
index|[]
name|type
decl_stmt|;
comment|/**      * Create a new instance based on arrays for the offsets and types.      *<p>      *<em>Note:</em> for performance reasons these arrays are directly referenced      * by this class and must not anymore be modified from other places.      *      * @param offsets  Offsets per position. -1 if not mapped.      * @param type     Types per position. Not defined if not mapped.      */
specifier|public
name|ImmutableRecordNumbers
parameter_list|(
annotation|@
name|NotNull
name|int
index|[]
name|offsets
parameter_list|,
annotation|@
name|NotNull
name|byte
index|[]
name|type
parameter_list|)
block|{
name|this
operator|.
name|offsets
operator|=
name|offsets
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
name|getOffset
parameter_list|(
name|int
name|recordNumber
parameter_list|)
block|{
if|if
condition|(
name|recordNumber
operator|<
name|offsets
operator|.
name|length
condition|)
block|{
return|return
name|offsets
index|[
name|recordNumber
index|]
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|Entry
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Entry
name|computeNext
parameter_list|()
block|{
while|while
condition|(
operator|++
name|pos
operator|<
name|offsets
operator|.
name|length
operator|&&
name|offsets
index|[
name|pos
index|]
operator|<
literal|0
condition|)
block|{ }
if|if
condition|(
name|pos
operator|<
name|offsets
operator|.
name|length
condition|)
block|{
return|return
operator|new
name|Entry
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRecordNumber
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offsets
index|[
name|pos
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|RecordType
name|getType
parameter_list|()
block|{
return|return
name|RecordType
operator|.
name|values
argument_list|()
index|[
name|type
index|[
name|pos
index|]
index|]
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

