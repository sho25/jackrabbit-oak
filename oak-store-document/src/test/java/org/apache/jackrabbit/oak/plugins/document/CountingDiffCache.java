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
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|public
class|class
name|CountingDiffCache
extends|extends
name|MemoryDiffCache
block|{
class|class
name|CountingLoader
implements|implements
name|Loader
block|{
specifier|private
name|Loader
name|delegate
decl_stmt|;
name|CountingLoader
parameter_list|(
name|Loader
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
name|String
name|call
parameter_list|()
block|{
name|incLoadCount
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|call
argument_list|()
return|;
block|}
block|}
specifier|private
name|int
name|loadCount
decl_stmt|;
specifier|public
name|CountingDiffCache
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|incLoadCount
parameter_list|()
block|{
name|loadCount
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|getLoadCount
parameter_list|()
block|{
return|return
name|loadCount
return|;
block|}
specifier|public
name|void
name|resetLoadCounter
parameter_list|()
block|{
name|loadCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getChanges
parameter_list|(
annotation|@
name|NotNull
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|NotNull
name|RevisionVector
name|to
parameter_list|,
annotation|@
name|NotNull
name|Path
name|path
parameter_list|,
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
block|{
return|return
name|super
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
operator|new
name|CountingLoader
argument_list|(
name|loader
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

