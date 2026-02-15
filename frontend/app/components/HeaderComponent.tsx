import Link from "next/link";

export default function HeaderComponent() {
  return (
    <nav className="navbar navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" href="/">
          Employee Management System
        </Link>
      </div>
    </nav>
  );
}
