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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_class
class|class
name|LuceneDoc
implements|implements
name|LuceneDocInfo
block|{
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|final
name|String
name|docPath
decl_stmt|;
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
decl_stmt|;
specifier|final
name|boolean
name|delete
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|processed
decl_stmt|;
specifier|public
specifier|static
name|LuceneDoc
name|forUpdate
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|path
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|)
block|{
return|return
operator|new
name|LuceneDoc
argument_list|(
name|indexPath
argument_list|,
name|path
argument_list|,
name|checkNotNull
argument_list|(
name|doc
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|LuceneDoc
name|forDelete
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|LuceneDoc
argument_list|(
name|indexPath
argument_list|,
name|path
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|LuceneDoc
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{
name|this
operator|.
name|docPath
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|checkNotNull
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|delete
operator|=
name|delete
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s(%s)"
argument_list|,
name|indexPath
argument_list|,
name|docPath
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isProcessed
parameter_list|()
block|{
return|return
name|processed
return|;
block|}
specifier|public
name|void
name|markProcessed
parameter_list|()
block|{
name|processed
operator|=
literal|true
expr_stmt|;
block|}
comment|//~-------------------------------< LuceneDocInfo>
annotation|@
name|Override
specifier|public
name|String
name|getIndexPath
parameter_list|()
block|{
return|return
name|indexPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDocPath
parameter_list|()
block|{
return|return
name|docPath
return|;
block|}
block|}
end_class

end_unit

