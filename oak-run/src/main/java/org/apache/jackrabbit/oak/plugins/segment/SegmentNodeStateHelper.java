begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|segment
package|;
end_package

begin_class
specifier|public
class|class
name|SegmentNodeStateHelper
block|{
specifier|private
name|SegmentNodeStateHelper
parameter_list|()
block|{      }
specifier|public
specifier|static
name|RecordId
name|getTemplateId
parameter_list|(
name|SegmentNodeState
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|getTemplateId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

