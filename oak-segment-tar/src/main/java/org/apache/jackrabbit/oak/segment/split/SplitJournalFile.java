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
name|split
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
name|oak
operator|.
name|segment
operator|.
name|spi
operator|.
name|persistence
operator|.
name|JournalFile
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
name|spi
operator|.
name|persistence
operator|.
name|JournalFileReader
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
name|spi
operator|.
name|persistence
operator|.
name|JournalFileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_class
specifier|public
class|class
name|SplitJournalFile
implements|implements
name|JournalFile
block|{
specifier|private
specifier|final
name|JournalFile
name|roJournalFile
decl_stmt|;
specifier|private
specifier|final
name|JournalFile
name|rwJournalFile
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|lastRoJournalEntry
decl_stmt|;
specifier|public
name|SplitJournalFile
parameter_list|(
name|JournalFile
name|roJournalFile
parameter_list|,
name|JournalFile
name|rwJournalFile
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|lastRoJournalEntry
parameter_list|)
block|{
name|this
operator|.
name|roJournalFile
operator|=
name|roJournalFile
expr_stmt|;
name|this
operator|.
name|rwJournalFile
operator|=
name|rwJournalFile
expr_stmt|;
name|this
operator|.
name|lastRoJournalEntry
operator|=
name|lastRoJournalEntry
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JournalFileReader
name|openJournalReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SplitJournalFileReader
argument_list|(
name|roJournalFile
operator|.
name|openJournalReader
argument_list|()
argument_list|,
name|rwJournalFile
operator|.
name|openJournalReader
argument_list|()
argument_list|,
name|lastRoJournalEntry
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JournalFileWriter
name|openJournalWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|rwJournalFile
operator|.
name|openJournalWriter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|rwJournalFile
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|roJournalFile
operator|.
name|exists
argument_list|()
operator|||
name|rwJournalFile
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
end_class

end_unit

