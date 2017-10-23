import React from 'react'
import { Scrollbars } from 'react-custom-scrollbars';

const renderThumbVerticalDefault = ({ style, ...props }) => {
    const finalStyle = {
        ...style,
        cursor: 'pointer',
        borderRadius: 0,
        backgroundColor: '#CCCCCC',
        width:11,
        height: 61,
        right: 10
    };
    return <div style={finalStyle} {...props} />;
}

const renderTrackVerticalDefault = ({ style, ...props }) => {
    const finalStyle = {
        ...style,
        right: 2,
        bottom: 2,
        top: 2,
        borderRadius: 3
        /*backgroundColor: '#f0f0f0'*/
    };
    return <div style={finalStyle} {...props} />;
}

const UpgradeScrollBar = ({children,styles}) => (
    <Scrollbars style={styles} hideTracksWhenNotNeeded={true}
        renderTrackVertical={renderTrackVerticalDefault}
        renderThumbVertical={renderThumbVerticalDefault}
    ><div style={{paddingRight:20}}>
        {children}
    </div></Scrollbars>
)

export default UpgradeScrollBar
