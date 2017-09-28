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
operator|.
name|standby
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|segment
operator|.
name|Segment
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
name|segment
operator|.
name|SegmentId
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
name|segment
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_class
class|class
name|DefaultStandbyReferencesReader
implements|implements
name|StandbyReferencesReader
block|{
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
name|DefaultStandbyReferencesReader
parameter_list|(
name|FileStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|readReferences
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|long
name|msb
init|=
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|SegmentId
name|segmentId
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|.
name|containsSegment
argument_list|(
name|segmentId
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|references
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segment
operator|.
name|getReferencedSegmentIdCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|references
operator|.
name|add
argument_list|(
name|segment
operator|.
name|getReferencedSegmentId
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|references
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

