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
name|query
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|commons
operator|.
name|PathUtils
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
name|Cursor
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
name|Filter
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
name|QueryIndex
import|;
end_import

begin_comment
comment|/**  * An index that traverses over a given subtree.  */
end_comment

begin_class
specifier|public
class|class
name|TraversingIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|final
name|MicroKernel
name|mk
decl_stmt|;
specifier|private
name|int
name|childBlockSize
init|=
literal|2000
decl_stmt|;
specifier|public
name|TraversingIndex
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
block|}
specifier|public
name|void
name|setChildBlockSize
parameter_list|(
name|int
name|childBlockSize
parameter_list|)
block|{
name|this
operator|.
name|childBlockSize
operator|=
name|childBlockSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
return|return
operator|new
name|TraversingCursor
argument_list|(
name|mk
argument_list|,
name|revisionId
argument_list|,
name|childBlockSize
argument_list|,
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|String
name|path
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// TODO estimate or read the node count
name|double
name|nodeCount
init|=
literal|10000000
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|depth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
init|;
name|depth
operator|>
literal|0
condition|;
name|depth
operator|--
control|)
block|{
comment|// estimate 10 child nodes per node
name|nodeCount
operator|/=
literal|10
expr_stmt|;
block|}
block|}
return|return
name|nodeCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|String
name|p
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|r
init|=
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|p
operator|=
literal|""
expr_stmt|;
block|}
return|return
literal|"traverse \""
operator|+
name|p
operator|+
name|r
operator|+
literal|'"'
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"traverse"
return|;
block|}
block|}
end_class

end_unit

