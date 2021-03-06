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
name|file
operator|.
name|tar
operator|.
name|index
package|;
end_package

begin_class
specifier|public
class|class
name|SimpleIndexEntry
implements|implements
name|IndexEntry
block|{
specifier|private
specifier|final
name|long
name|msb
decl_stmt|;
specifier|private
specifier|final
name|long
name|lsb
decl_stmt|;
specifier|private
specifier|final
name|int
name|position
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|private
specifier|final
name|int
name|generation
decl_stmt|;
specifier|private
specifier|final
name|int
name|fullGeneration
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|compacted
decl_stmt|;
specifier|public
name|SimpleIndexEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|generation
parameter_list|,
name|int
name|fullGeneration
parameter_list|,
name|boolean
name|compacted
parameter_list|)
block|{
name|this
operator|.
name|msb
operator|=
name|msb
expr_stmt|;
name|this
operator|.
name|lsb
operator|=
name|lsb
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
name|this
operator|.
name|fullGeneration
operator|=
name|fullGeneration
expr_stmt|;
name|this
operator|.
name|compacted
operator|=
name|compacted
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMsb
parameter_list|()
block|{
return|return
name|msb
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLsb
parameter_list|()
block|{
return|return
name|lsb
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getGeneration
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getFullGeneration
parameter_list|()
block|{
return|return
name|fullGeneration
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCompacted
parameter_list|()
block|{
return|return
name|compacted
return|;
block|}
block|}
end_class

end_unit

