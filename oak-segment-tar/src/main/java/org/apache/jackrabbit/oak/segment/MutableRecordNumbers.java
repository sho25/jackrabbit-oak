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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|copyOf
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|fill
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
comment|/**  * A thread-safe, mutable record table.  */
end_comment

begin_class
class|class
name|MutableRecordNumbers
implements|implements
name|RecordNumbers
block|{
specifier|private
name|int
index|[]
name|recordEntries
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|public
name|MutableRecordNumbers
parameter_list|()
block|{
name|recordEntries
operator|=
operator|new
name|int
index|[
literal|16384
index|]
expr_stmt|;
name|fill
argument_list|(
name|recordEntries
argument_list|,
operator|-
literal|1
argument_list|)
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
name|int
name|recordEntry
init|=
name|getRecordEntry
argument_list|(
name|recordEntries
argument_list|,
name|recordNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|recordEntry
operator|==
operator|-
literal|1
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|recordEntry
operator|=
name|getRecordEntry
argument_list|(
name|recordEntries
argument_list|,
name|recordNumber
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|recordEntry
return|;
block|}
specifier|private
specifier|static
name|int
name|getRecordEntry
parameter_list|(
name|int
index|[]
name|entries
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|index
operator|*
literal|2
operator|>=
name|entries
operator|.
name|length
condition|?
operator|-
literal|1
else|:
name|entries
index|[
name|index
operator|*
literal|2
index|]
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
specifier|synchronized
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
specifier|final
name|int
index|[]
name|entries
init|=
name|copyOf
argument_list|(
name|recordEntries
argument_list|,
name|size
operator|*
literal|2
argument_list|)
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Entry
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|index
operator|<
name|entries
operator|.
name|length
condition|)
block|{
return|return
operator|new
name|Entry
argument_list|()
block|{
specifier|final
name|int
name|recordNumber
init|=
name|index
operator|/
literal|2
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|entries
index|[
name|index
operator|++
index|]
decl_stmt|;
specifier|final
name|RecordType
name|type
init|=
name|RecordType
operator|.
name|values
argument_list|()
index|[
name|entries
index|[
name|index
operator|++
index|]
index|]
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|getRecordNumber
parameter_list|()
block|{
return|return
name|recordNumber
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
name|offset
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
name|type
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
comment|/**      * Return the size of this table.      *      * @return the size of this table.      */
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Add a new offset to this table and generate a record number for it.      *      * @param type   the type of the record.      * @param offset an offset to be added to this table.      * @return the record number associated to the offset.      */
specifier|synchronized
name|int
name|addRecord
parameter_list|(
name|RecordType
name|type
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
if|if
condition|(
name|recordEntries
operator|.
name|length
operator|<=
name|size
operator|*
literal|2
condition|)
block|{
name|recordEntries
operator|=
name|copyOf
argument_list|(
name|recordEntries
argument_list|,
name|recordEntries
operator|.
name|length
operator|*
literal|2
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|recordEntries
argument_list|,
name|recordEntries
operator|.
name|length
operator|/
literal|2
argument_list|,
name|recordEntries
operator|.
name|length
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|recordEntries
index|[
literal|2
operator|*
name|size
index|]
operator|=
name|offset
expr_stmt|;
name|recordEntries
index|[
literal|2
operator|*
name|size
operator|+
literal|1
index|]
operator|=
name|type
operator|.
name|ordinal
argument_list|()
expr_stmt|;
return|return
name|size
operator|++
return|;
block|}
block|}
end_class

end_unit

