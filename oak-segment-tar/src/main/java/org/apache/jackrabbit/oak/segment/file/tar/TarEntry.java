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
name|segment
operator|.
name|file
operator|.
name|tar
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * A file entry location in a tar file. This is used for the index with a tar  * file.  */
end_comment

begin_class
class|class
name|TarEntry
block|{
comment|/** Size in bytes a tar entry takes up in the tar file */
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|33
decl_stmt|;
specifier|static
specifier|final
name|Comparator
argument_list|<
name|TarEntry
argument_list|>
name|OFFSET_ORDER
init|=
operator|new
name|Comparator
argument_list|<
name|TarEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|TarEntry
name|a
parameter_list|,
name|TarEntry
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|offset
operator|>
name|b
operator|.
name|offset
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|offset
operator|<
name|b
operator|.
name|offset
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
decl_stmt|;
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
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
specifier|private
specifier|final
name|GCGeneration
name|generation
decl_stmt|;
name|TarEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|GCGeneration
name|generation
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
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
block|}
name|long
name|msb
parameter_list|()
block|{
return|return
name|msb
return|;
block|}
name|long
name|lsb
parameter_list|()
block|{
return|return
name|lsb
return|;
block|}
name|int
name|offset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
name|GCGeneration
name|generation
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
block|}
end_class

end_unit

