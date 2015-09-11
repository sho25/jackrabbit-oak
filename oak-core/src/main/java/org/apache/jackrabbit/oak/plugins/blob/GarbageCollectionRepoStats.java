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
name|blob
package|;
end_package

begin_comment
comment|/**  * Garbage collection stats for the repository.  */
end_comment

begin_class
specifier|public
class|class
name|GarbageCollectionRepoStats
block|{
specifier|private
name|String
name|repositoryId
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|long
name|endTime
decl_stmt|;
specifier|private
name|long
name|length
decl_stmt|;
specifier|private
name|int
name|numLines
decl_stmt|;
specifier|public
name|String
name|getRepositoryId
parameter_list|()
block|{
return|return
name|repositoryId
return|;
block|}
specifier|public
name|void
name|setRepositoryId
parameter_list|(
name|String
name|repositoryId
parameter_list|)
block|{
name|this
operator|.
name|repositoryId
operator|=
name|repositoryId
expr_stmt|;
block|}
specifier|public
name|long
name|getEndTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
specifier|public
name|void
name|setEndTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
block|}
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|void
name|setNumLines
parameter_list|(
name|int
name|numLines
parameter_list|)
block|{
name|this
operator|.
name|numLines
operator|=
name|numLines
expr_stmt|;
block|}
specifier|public
name|int
name|getNumLines
parameter_list|()
block|{
return|return
name|numLines
return|;
block|}
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
block|}
end_class

end_unit

