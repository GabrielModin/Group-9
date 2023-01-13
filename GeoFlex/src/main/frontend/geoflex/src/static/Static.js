import React from 'react';
import { Outlet } from 'react-router-dom';
import Cookies from 'universal-cookie';
import Navbar from '../shared/Navbar';

const cookies = new Cookies();

export default function Static() {

  let status = cookies.get('role')
  let content;
  if (status === 'moderator') {
    content = <Navbar type={'mod'} />
  } else if (status === 'admin') {
    content = <Navbar type={'admin'} />
  } else {
    content = ""
  }
  return (
    <>
      <div className="row">
        {content}
        <Outlet />
      </div>
      <div className='row'>
        <footer>
          <div aria-label="footer" className="col-12">
            <ul className="row center-align container">
              <li className='col s4 white-text text-darken-2'>© 2023 GeoFlex</li>
              <li className='col s4'><a className="white-text text-darken-2" href="/faq">FAQ</a></li>
              <li className='col s4'><a className="white-text text-darken-2" href="/about">Om oss</a></li>
            </ul>
          </div>
        </footer>
      </div>
    </>
  )
}


